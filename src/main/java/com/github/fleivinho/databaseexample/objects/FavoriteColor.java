package com.github.fleivinho.databaseexample.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FavoriteColor {

    NONE("Nenhuma", "§f"), BLUE("Azul", "§b"), RED("Vermelho", "§c"), PINK("Rosa", "§d"), BLACK("Preto", "§0");

    private final String name, minecraftColor;

}
