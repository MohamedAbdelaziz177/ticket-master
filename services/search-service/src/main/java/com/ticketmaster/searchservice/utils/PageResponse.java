package com.ticketmaster.searchservice.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter @Setter @AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<E, R> {

    List<R> content;
    long totalElements;
    int totalPages;
    int pageNumber;
    int pageSize;

    public static <E, R> PageResponse<E, R> of(
            long totalElements,
            int pageNumber,
            int pageSize,
            List<R> content
    ) {
        int totalPages = pageSize < 1 ? 0 : (int) ((totalElements + pageSize - 1) / pageSize);

        return new PageResponse<>(
                content,
                totalElements,
                totalPages,
                pageNumber,
                pageSize
        );
    }
}
