package ru.yandex.practicum.dto.shoppingStore;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Данный класс реализован лишь из-за того, что при Postman-тестах объект Page при сериализации
// в json возвращает неправильный набор полей в объекте Sort
@Getter @ToString
public class PageResponse<T> {
    private final List<T> content;
    private final long totalElements;
    private final int totalPages;
    private final boolean last;
    private final int size;
    private final int number;
    private final Set<SortView> sort = new HashSet<>();
    private final int numberOfElements;
    private final boolean first;

    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
        this.size = page.getSize();
        this.number = page.getNumber();
        this.numberOfElements = page.getNumberOfElements();
        this.first = page.isFirst();

        for (var order : page.getSort().toSet()) {
            sort.add(new SortView(List.of(order)));
        }
    }

    @Getter @ToString
    static class SortView {
        private final Sort.Direction direction;
        private final String property;
        private final boolean ignoreCase;
        private final Sort.NullHandling nullHandling;
        private final boolean ascending;
        private final boolean descending;

        public SortView(List<Sort.Order> orders) {
            this.direction = orders.getFirst().getDirection();
            this.property = orders.getFirst().getProperty();
            this.ignoreCase = orders.getFirst().isIgnoreCase();
            this.nullHandling = orders.getFirst().getNullHandling();
            this.ascending = orders.getFirst().isAscending();
            this.descending = orders.getFirst().isDescending();
        }
    }
}
