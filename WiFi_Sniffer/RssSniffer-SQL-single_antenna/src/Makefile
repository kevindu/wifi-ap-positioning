LDFLAGS+=-L$(STAGING_DIR)/usr/lib/mysql

RssSniffer-SQL: RssSniffer-SQL.o
	$(CC) $(LDFLAGS) RssSniffer-SQL.o -o RssSniffer-SQL -lpcap -lmysqlclient -lpthread -lz -lm -ldl
RssSniffer-SQL.o: RssSniffer-SQL.c
	$(CC) $(CFLAGS) -c RssSniffer-SQL.c

clean:
	rm *.o RssSniffer-SQL
