package com.app.grabfoodapp.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private int page;
    private int size;
    private long total;
    private T items;
}
