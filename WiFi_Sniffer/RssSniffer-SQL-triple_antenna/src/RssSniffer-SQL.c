/*
 * File:   main.c
 * Author: Jiuzhou Wu, Xuan Du
 *
 * Created on 19 June 2015, 19:39
 * Revised on 1 May 2017
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <pcap.h>
#include <time.h>
#include <inttypes.h>
#include <byteswap.h>
#include <netinet/ether.h>

#include <sys/ioctl.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <net/if.h>

#include <mysql/mysql.h>

struct ieee80211_radiotap_header {
    u_int8_t it_version;
    u_int8_t it_pad;
    u_int16_t it_len;
    u_int32_t it_present;
} __attribute__((__packed__));

struct rtapdata {
    uint64_t tsft;
    uint8_t flags;

    uint8_t rate;

    uint16_t c_frequency;
    uint16_t c_flags;
    uint16_t antsignal;

    //uint8_t antenna_index;

    uint16_t rx_flags;

    uint8_t antsignal0;
    uint8_t antenna_index0;
    uint8_t antsignal1;
    uint8_t antenna_index1;
    uint8_t antsignal2;
    uint8_t antenna_index2;

} __attribute__((packed));

MYSQL *con;

void finish_with_error(MYSQL *con);

char* get_mac();

void str_upper(char arr[]);

/* prototype of the packet handler  */
void packet_handler(u_char *param, const struct pcap_pkthdr *header, const u_char *pkt_data);

char dev[] = "mon0"; // Set a device to sniff on

char errbuf[PCAP_ERRBUF_SIZE]; // Error message

struct bpf_program fp; /* The compiled filter expression */

char filter_exp[] = "subtype probe-req"; // filter expression

pcap_t *session_handler; // sniffing session handler

bpf_u_int32 mask; // The netmask of our sniffing device
bpf_u_int32 net; // The IP of our sniffing device

char ap_mac_addr[18];

unsigned long MAC_LENGTH = 17;

int pkt_id = 1; // count the number of packets captured

char *database_host;
char *database_username;
char *database_password;

int main(int argc, char** argv) {

  database_host = argv[1];
  database_username = argv[2];
  database_password = argv[3];

    //Get the MAC address of the AP
    char *temp_arr = get_mac();
    strncpy(ap_mac_addr, temp_arr, 17);
    ap_mac_addr[17] = '\0';
    str_upper(ap_mac_addr);
    free(temp_arr);

    system("iw phy phy0 interface add mon0 type monitor");
    system("ifconfig mon0 up");

    session_handler = pcap_open_live(dev, 65535, 1, 1000, errbuf);

    if (session_handler == NULL) {
        fprintf(stderr, "Couldn't open device %s: %s\n", dev, errbuf);
        return (2);
    }

    printf("Link-layer header type: %d\n", pcap_datalink(session_handler));

    if (pcap_datalink(session_handler) != DLT_IEEE802_11_RADIO) {
        printf("Device: %d\n", pcap_datalink(session_handler));
        fprintf(stderr, "Device %s doesn't provide IEEE 802.11 Radiotap Capture headers\n", dev);
        return (2);
    }

    // Read the network mask and the IP of the sniffing device
    //    if (pcap_lookupnet(dev, &net, &mask, errbuf) == -1) {
    //        fprintf(stderr, "Can't get netmask for device %s\n", dev);
    //        net = 0;
    //        mask = 0;
    //    }

    if (pcap_compile(session_handler, &fp, filter_exp, 0, net) == -1) {
        fprintf(stderr, "Couldn't parse filter %s: %s\n", filter_exp, pcap_geterr(session_handler));
        return (2);
    }

    if (pcap_setfilter(session_handler, &fp) == -1) {
        fprintf(stderr, "Couldn't install filter %s: %s\n", filter_exp, pcap_geterr(session_handler));
        return (2);
    }

    con = mysql_init(NULL);
    if (con == NULL) {
        fprintf(stderr, "%s\n", mysql_error(con));
        exit(1);
    }

    if (mysql_real_connect(con, database_host, database_username,database_password,
            "myips", 0, NULL, 0) == NULL) {
        fprintf(stderr, "%s\n", mysql_error(con));
        mysql_close(con);
        exit(1);
    }

    pcap_loop(session_handler, -1, packet_handler, NULL);
    pcap_close(session_handler);
    mysql_close(con);

    return (EXIT_SUCCESS);
}

/* callback function to process a packet when captured */
void packet_handler(u_char *param, const struct pcap_pkthdr *header, const u_char *pkt_data) {
    (void) (param);

    struct tm ts;
    // get the timestamp from the packet header
    time_t pkthdr_ts;
    pkthdr_ts = header->ts.tv_sec;

    /* convert the timestamp to readable format */
    //time(&pkthdr_ts);
    ts = *localtime(&pkthdr_ts);

    char time_buf[20];
    printf("No. %d  ", pkt_id);

    strftime(time_buf, sizeof (time_buf), "%Y-%m-%d %H:%M:%S", &ts);
    printf("Last Seen: %s Len: %d\n", time_buf, header->caplen);

    struct ieee80211_radiotap_header* radiotap_hdr;

    // Skip the datalink layer header
    //pkt_data+=24;
    radiotap_hdr = (struct ieee80211_radiotap_header*) pkt_data;
    //printf("version: %" PRIu8 "\n", radiotap_hdr->it_version);
    //printf("pad: %" PRIu8 "\n", radiotap_hdr->it_pad);
    //printf("length: %" PRIu16 "\n", __bswap_16(radiotap_hdr->it_len));
    //printf("present: %" PRIu32 "\n", __bswap_32(radiotap_hdr->it_present));

    pkt_data += 24;

    struct rtapdata* rtap_data;
    rtap_data = (struct rtapdata*) pkt_data;

    //printf("MAC timestamp: %" PRIu64 "\n",__bswap_64(rtap_data->tsft));
    //printf("flag:  0x%02x \n", rtap_data->flags);
    printf("rate: %.1f Mb/s\n", (float) rtap_data->rate * 500 / 1000);
    printf("Channel freq: %" PRIu8 "\n", __bswap_16(rtap_data->c_frequency));
    // printf("Channel flags:  0x%04x \n", __bswap_16(rtap_data->c_flags));
    printf("Antenna signal: %d dBm\n", __bswap_16((rtap_data->antsignal)) - 256);
    //printf("Antenna index: %d \n", rtap_data->antenna_index);
    //printf("RX flag: 0x%04x \n", __bswap_16(rtap_data->rx_flags));

    ////
    int rssi = __bswap_16((rtap_data->antsignal)) - 256;
    ////

    printf("SSI signal: %d dBm\n", (rtap_data->antsignal0) - 256);
    printf("Antenna index: %d \n", rtap_data->antenna_index0);

    printf("SSI signal: %d dBm\n", (rtap_data->antsignal1) - 256);
    printf("Antenna index: %d \n", rtap_data->antenna_index1);

    printf("SSI signal: %d dBm\n", (rtap_data->antsignal2) - 256);
    printf("Antenna index: %d \n", rtap_data->antenna_index2);

    pkt_data = pkt_data - 24 + __bswap_16(radiotap_hdr->it_len) + 4;

    struct ether_header* eth_hdr;
    eth_hdr = (struct ether_header*) pkt_data;
    //char des_addr[18];
    char src_addr[18];

    //sprintf(des_addr, "%02x:%02x:%02x:%02x:%02x:%02x", eth_hdr->ether_dhost[0], eth_hdr->ether_dhost[1], eth_hdr->ether_dhost[2], eth_hdr->ether_dhost[3], eth_hdr->ether_dhost[4], eth_hdr->ether_dhost[5]);
    sprintf(src_addr, "%02x:%02x:%02x:%02x:%02x:%02x", eth_hdr->ether_shost[0], eth_hdr->ether_shost[1], eth_hdr->ether_shost[2], eth_hdr->ether_shost[3], eth_hdr->ether_shost[4], eth_hdr->ether_shost[5]);

    //str_upper(des_addr);
    str_upper(src_addr);

        printf("Device_MAC: %s\n", ap_mac_addr);
    printf("Detected_Device: %s\n", src_addr);
    //printf("time: %d\n", pkthdr_ts);


    printf("-------------------\n");

    ////////////////////////MYSQL///////////////////////////

    MYSQL_BIND parameter[6];
    MYSQL_STMT *stmt;
    char query[255] = "INSERT INTO detected_devices (dev_mac_addr,ap_mac_addr,signal_strength,time) VALUES(?,?,?,FROM_UNIXTIME(?)) ON DUPLICATE KEY UPDATE signal_strength=?, time=FROM_UNIXTIME(?)";

    stmt = mysql_stmt_init(con);

    if (!stmt) {
        fprintf(stderr, " mysql_stmt_init(), out of memory\n");
        exit(0);
    }

    if (mysql_stmt_prepare(stmt, query, strlen(query))) {
        fprintf(stderr, "\n mysql_stmt_prepare(), INSERT failed");
        fprintf(stderr, "\n %s", mysql_stmt_error(stmt));
        exit(0);
    }
    memset(parameter, 0, sizeof (parameter));

    parameter[0].buffer_type = MYSQL_TYPE_VAR_STRING;
    parameter[0].buffer = (char *) src_addr;
    parameter[0].is_null = 0;
    parameter[0].buffer_length = 18;
    parameter[0].length = &MAC_LENGTH;

    parameter[1].buffer_type = MYSQL_TYPE_VAR_STRING;
    parameter[1].buffer = (char *) ap_mac_addr;
    parameter[1].is_null = 0;
    parameter[1].buffer_length = 18;
    parameter[1].length = &MAC_LENGTH;

    parameter[2].buffer_type = MYSQL_TYPE_LONG;
    parameter[2].buffer = (char *) &rssi;
    parameter[2].is_null = 0;
    parameter[2].length = 0;


    parameter[3].buffer_type = MYSQL_TYPE_LONG;
    parameter[3].buffer = (char *) &pkthdr_ts;
    parameter[3].is_null = 0;
    parameter[3].length = 0;

    parameter[4] = parameter[2];
    parameter[5] = parameter[3];

    if (mysql_stmt_bind_param(stmt, parameter)) {
        fprintf(stderr, " mysql_stmt_bind_param() failed/n");
        fprintf(stderr, " %s/n", mysql_stmt_error(stmt));
        exit(0);
    }

    if (mysql_stmt_execute(stmt)) {
        fprintf(stderr, " mysql_stmt_execute(), 1 failed/n");
        fprintf(stderr, " %s/n", mysql_stmt_error(stmt));
        exit(0);
    }

    if (mysql_stmt_close(stmt)) {
        fprintf(stderr, " failed while closing the statement/n");
        fprintf(stderr, " %s/n", mysql_stmt_error(stmt));
        exit(0);
    }

    MYSQL_BIND parameter1[1];
    // MYSQL_STMT *stmt;
    char query1[255] = "INSERT INTO statistics (dev_mac_addr) VALUES(?) ON DUPLICATE KEY UPDATE Frequency=Frequency+1";

    stmt = mysql_stmt_init(con);

    if (!stmt) {
        fprintf(stderr, " mysql_stmt_init(), out of memory\n");
        exit(0);
    }

    if (mysql_stmt_prepare(stmt, query1, strlen(query1))) {
        fprintf(stderr, "\n mysql_stmt_prepare(), INSERT failed");
        fprintf(stderr, "\n %s", mysql_stmt_error(stmt));
        exit(0);
    }
    memset(parameter1, 0, sizeof (parameter1));

    parameter1[0].buffer_type = MYSQL_TYPE_VAR_STRING;
    parameter1[0].buffer = (char *) src_addr;
    parameter1[0].is_null = 0;
    parameter1[0].buffer_length = 18;
    parameter1[0].length = &MAC_LENGTH;

    if (mysql_stmt_bind_param(stmt, parameter1)) {
        fprintf(stderr, " mysql_stmt_bind_param() failed/n");
        fprintf(stderr, " %s/n", mysql_stmt_error(stmt));
        exit(0);
    }

    //mysql_stmt_bind_param(stmt, parameter);

    if (mysql_stmt_execute(stmt)) {
        fprintf(stderr, " mysql_stmt_execute(), 1 failed/n");
        fprintf(stderr, " %s/n", mysql_stmt_error(stmt));
        exit(0);
    }

    if (mysql_stmt_close(stmt)) {
        fprintf(stderr, " failed while closing the statement/n");
        fprintf(stderr, " %s/n", mysql_stmt_error(stmt));
        exit(0);
    }

//    if (pkt_id >= 20000) {
//        pcap_breakloop(session_handler);
//    }
    pkt_id++;
}

char* get_mac() {
    struct ifreq ifreq;
    int sock;
    char interface[] = "wlan0";
    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        perror("socket ");
        //return 2;
    }
    strcpy(ifreq.ifr_name, interface);
    if (ioctl(sock, SIOCGIFHWADDR, &ifreq) < 0) {
        perror("ioctl ");
        //return 3;
    }

    char *mac_addr = malloc(sizeof (char)*18);

    sprintf(mac_addr, "%02x:%02x:%02x:%02x:%02x:%02x\n ",
            (unsigned char) ifreq.ifr_hwaddr.sa_data[0],
            (unsigned char) ifreq.ifr_hwaddr.sa_data[1],
            (unsigned char) ifreq.ifr_hwaddr.sa_data[2],
            (unsigned char) ifreq.ifr_hwaddr.sa_data[3],
            (unsigned char) ifreq.ifr_hwaddr.sa_data[4],
            (unsigned char) ifreq.ifr_hwaddr.sa_data[5]);
    close(sock);
    return mac_addr;
}

void str_upper(char arr[]) {
    int i = 0;
    while (arr[i] != '\0') {
        arr[i] = toupper(arr[i]);
        i++;
    }
}

void finish_with_error(MYSQL *con) {
    fprintf(stderr, "%s\n", mysql_error(con));
    mysql_close(con);
    exit(1);
}
