package com.meldateksari.eticaret.dto;

import jdk.jshell.Snippet;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryDto {
    // Getters and Setters
    private Long id;
    private String name;
    private String slug;
    private String description;
    private Long parentId;



}
