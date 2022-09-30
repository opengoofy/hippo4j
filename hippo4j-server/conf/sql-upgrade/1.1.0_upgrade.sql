ALTER TABLE `config` DROP INDEX `uk_configinfo_datagrouptenant`;

ALTER TABLE `item` DROP INDEX `uk_iteminfo_tenantitem`;

ALTER TABLE `tenant` DROP INDEX `uk_tenantinfo_tenantid`;
