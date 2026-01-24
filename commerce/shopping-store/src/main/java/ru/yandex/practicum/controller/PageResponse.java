package ru.yandex.practicum.controller;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Stream;

// Данный класс реализован лишь из-за того, что при Postman-тестах объект Page при сериализации
// в json возвращает неправильный набор полей в объекте Sort
@Getter @ToString
public class PageResponse<T> {
    private final int totalPages;
    private final long totalElements;
    private final int size;
    private final List<T> content;
    private final int number;
    private final SortView sort;
    private final int numberOfElements;
    private final boolean first;
    private final boolean last;
    private final PageableView pageable;
    private final boolean empty;

    public PageResponse(Page<T> page) {
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.size = page.getSize();
        this.content = page.getContent();
        this.number = page.getNumber();
        this.sort = new SortView(page.getSort());
        this.numberOfElements = page.getNumberOfElements();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.pageable = setPageable(page.getPageable());
        this.empty = page.isEmpty();
    }

    private PageableView setPageable(Pageable pageable) {
        return new PageableView(pageable);
    }

    @Getter @ToString
    static class SortView {
        private final Stream<Sort.Order> orders;
        private final boolean sorted;
        private final boolean unsorted;
        private final boolean empty;

        public SortView(Sort sort) {
            this.orders = sort.stream();
            this.sorted = sort.isSorted();
            this.unsorted = sort.isUnsorted();
            this.empty = sort.isEmpty();
        }
    }

    @Getter @ToString
    static class PageableView {
        private final int pageNumber;
        private final int pageSize;
        private final SortView sort;
        private final long offset;
        private final boolean paged;
        private final boolean unpaged;

        public PageableView(Pageable pageable) {
            this.pageNumber = pageable.getPageNumber();
            this.pageSize = pageable.getPageSize();
            this.sort = new SortView(pageable.getSort());
            this.offset = pageable.getOffset();
            this.paged = pageable.isPaged();
            this.unpaged = pageable.isUnpaged();
        }
    }
}
