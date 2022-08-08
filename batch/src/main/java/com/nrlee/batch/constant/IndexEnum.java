package com.nrlee.batch.constant;

import lombok.Getter;

public enum IndexEnum {

    WINE("wine", "wine-write", "/mapping/mappings.json", "/setting/settings.json");

    @Getter
    private final String readAlias;
    @Getter
    private final String writeAlias;
    @Getter
    private final String mappingJsonPath;
    @Getter
    private final String settingJsonPath;

    IndexEnum(String readAlias, String writeAlias, String mappingJsonPath, String settingJsonPath) {
        this.readAlias = readAlias;
        this.writeAlias = writeAlias;
        this.mappingJsonPath = mappingJsonPath;
        this.settingJsonPath = settingJsonPath;
    }
}
