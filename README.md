# WiFi AP-centred Positioning
Indoor positioning systems have gained a lot of attention in the last few years. With the introduction of the Internet of Things (IoT) paradigm the knowledge of user's location has become a crucial information to deliver efficient and tailored Location Based Services (LBS), especially in indoor environments. 

Here we propose an AP (Access Point)-centred indoor positioning system that overcomes common limitations presented in conventional positioning systems, such as an excessive involvement of Mobile Devices (MDs). Our work merges ideas originally proposed in [1] and [2] to build an efficient, accurate and smart Probabilistic-FingerPrint (P-FP) algorithm that avoids the MD involvement and considers the signal strength measurements as a random variable in the positioning process. Numerical results, obtained in a real-world deployment, show better performance on positioning accuracy, energy consumption and latency with respect to the MD-based architecture.

The related architecture and algorithms in this work are published and you can find more details in the following papers.
1. X. Du, J. Wu, K. Yang and L. Wang, "An AP-Centred Indoor Positioning System Combining Fingerprint Technique," 2016 IEEE Global Communications Conference (GLOBECOM), Washington, DC, 2016, pp. 1-6.
2. Igor Bisio, Fabio Lavagetto, Mario Marchese, Andrea Sciarrone, "Smart probabilistic fingerprinting for WiFi-based indoor positioning with mobile devices", Pervasive and Mobile Computing, Available online 24 February 2016, ISSN 1574-1192
3. X. Du, K. Yang, I. Bisio, F. Lavagetto and A. Sciarrone, "An AP-centred Smart Probabilistic Fingerprint System for Indoor Positioning", 2018 IEEE International Conference on Communications (ICC), Kansa City, USA, 2018

## Components
* WiFi Sniffer (C code) - running on WiFi AP
* Fingerprint Database (MySQL) - running on cloud server (which can be either edge or central cloud)
* Fingerprint Collector (Android App) - running on Android mobile device
* Positioning Engine (Java) - running on cloud server

## Get Started
### System Requirements
- System time between APs and server need to be synchronised. A local NTP server could be an option.
- All the components could communicate with each other.

#### NTP server setup:
```
/etc/init.d/sysntpd restart
ntpd -l
```

### Compile the WiFi Sniffer
Modify the antenna_index parameters in `WiFi_Sniffer/RssSniffer-SQL/src/RssSniffer-SQL.c` for different APs.

    make package/RssSniffer/compile
    make package/RssSniffer/install
    make package/RssSniffer

### Run the WiFi Sniffer
- An ipk package is generated under bin directory. e.g. RssSniffer_1.0_ar71xx.ipk
- Upload the ipk package to the openWrt of AP, install it via opkg. e.g. opkg install RssSniffer_1.0_ar71xx.ipk
- Run it via command: RssSniffer <database host/IP address> <database username> <database password>.
- The above command could also been running via a shell script, e.g. start_RssSniffer.sh

### Deploy Database
Install MySQL 5.6 server，create new database named myips，import myips_structure.sql to create the tables.
Add AP to the table access_points.

### Run the Fingerprint Collector
Configure the database information and indoor map.
```
con = DriverManager.getConnection("jdbc:mysql://host:3306/myips", "username", "password");
```
The RSS used by the Fingerprint Collector to build the fingerprint database in the training phase is the mobile device's RSS sensed by AP, i.e., RSS retrived from probe request frame by WiFi Sniffer, which is aligned with RSS detection mechanism in the positioning phase.

If the probabilistic algorithm is applied, the RSSs collected in the mobile phone are saved into a text file, which is uploaded to the server where the Positioning Engine is running.

### Positioning Engine
Configure the database information and indoor map.
```
con = DriverManager.getConnection("jdbc:mysql://host:3306/myips", "username", "password");
```
Generate the war package in IDE.
Deploy tomcat7 server, deploy the war package under the directory of tomcat, which usually is /var/lib/tomcat7/webapps/.

## Implementation Notes
### MH-based Positioning
In the implementation on Android platform, the information about surrounding detected APs are acquired by android.net.wifi.ScanResult class from WiFi service of Android. Each ScanResult is binding to a specific AP. From Android API level 17, the timestamp when the ScanResult was last seen is added to the ScanResult, which enables more fine
tuning of RSS observations. In our work the timestamp is always checked to ensure the RSS observed is latest or can be mapped to previous location based on moving trajectory. In addition, the frequency to obtain the latest ScanResult is not controlled by user while depending on performance of WiFi card. In our system implementation, a BroadcastReceiver is listening on the ScanResultsAvailableAction of WifiManager to receive the ScanResults. The example of usage can be found [here](https://github.com/kevindu/wifi-ap-positioning/blob/master/Fingerprint_Collector/MH-based/app/src/main/java/com/example/jiuzhou/fingerprintcollector/MainActivity.java).

## 中文文档/Docs in Chinese

### 系统组成模块：    
* Wi-Fi嗅探程序 （C） — 运行在Wi-Fi AP上
* 实时Wi-Fi设备信号强度数据库 （MySQL） — 云端服务器
* 指纹采集程序（Android App） — 运行在手机上
* 定位程序 （JSP） — 云端服务器

### 系统运行条件：
* Wi-Fi AP间需要时间同步。
* AP和手机采集程序可以与服务器通信，服务器部署在内网或外网均可。

如果AP无法连接到互联网的时钟服务器，建议在本地Wi-Fi网络中建立一个NTP时钟服务器。
NTP server
    /etc/init.d/sysntpd restart
    ntpd -l

基于RabbitMQ的升级版：
    Wi-Fi嗅探程序不再直接连接数据库，通过RabbitMQ消息队列发送到服务器上面的一个JAVA接收程序，从而再保存到数据库中。

#### 编译Wi-Fi嗅探程序
make package/RssSniffer/compile
make package/RssSniffer/install
make package/RssSniffer

modify the antenna_index parameters.

在bin的子目录下会生成相应的ipk按照包，例如：RssSniffer_1.0_ar71xx.ipk

将ipk包发送到openWrt系统里面，通过opkg安装：opkg install RssSniffer_1.0_ar71xx.ipk
安装成功后直接运行程序名称即可，RssSniffer-SQL <数据库地址> <数据库用户名> <数据库密码>。
通过start_RssSniffer.sh可以更方便地提供参数。

#### 部署数据库
安装MySQL 5.6服务器，创建新数据库myips，导入myips_structure.sql创建数据库结构。
添加AP到access_points表中。

#### 指纹采集程序
代码中配置数据库信息以及地图。

#### 定位程序
配置数据库信息及地图，在本地IDE中生成war包。
部署tomcat7服务器，将war包部署到tomcat，一般是放置到/var/lib/tomcat7/webapps/目录下。
