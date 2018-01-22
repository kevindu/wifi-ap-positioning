include $(TOPDIR)/rules.mk

PKG_NAME:=RssSniffer-SQL
PKG_RELEASE:=1.0

PKG_BUILD_DIR := $(BUILD_DIR)/$(PKG_NAME)

include $(INCLUDE_DIR)/package.mk

define Package/RssSniffer-SQL
	SECTION:=utils
	CATEGORY:=Utilities
	TITLE:=RssSniffer-SQL
	DEPENDS:=+libpcap +libmysqlclient +libpthread
endef

define Build/Prepare
	mkdir -p $(PKG_BUILD_DIR)
	$(CP) ./src/* $(PKG_BUILD_DIR)/
endef

define Package/RssSniffer-SQL/install
	$(INSTALL_DIR) $(1)/bin
	$(INSTALL_BIN) $(PKG_BUILD_DIR)/RssSniffer-SQL $(1)/bin/
endef

$(eval $(call BuildPackage,RssSniffer-SQL))
