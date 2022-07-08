package org.example.model.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PageMap {
    private Iterable data;
    private long totalPage;
    private long totalCount;
    private long page;
    private long size;

    private PageMap(Iterable data, long totalPage, long totalCount, long page, long size) {
        this.data = data;
        this.totalPage = totalPage;
        this.totalCount = totalCount;
        this.page = page;
        this.size = size;
    }

    public static PageMap of(Iterable data, long totalPage, long totalCount, long page, long size){
        return new PageMap(data, totalPage, totalCount, page, size);
    }
    public static PageMap of(Page page){
        return new PageMap(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
    }
}
