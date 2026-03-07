package com.study.loadtest.domain.event.model;

import com.study.loadtest.domain.event.exception.SoldOutException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventTest {

    @Test
    @DisplayName("재고 차감 성공: 잔여 수량이 충분하면 수량이 줄어든다")
    void decreaseQuantity_success() {
        // given
        Event event = new Event();
        event.setTotalQuantity(100);
        event.setRemainingQuantity(100);

        // when
        event.decreaseQuantity(10);

        // then
        assertThat(event.getRemainingQuantity()).isEqualTo(90);
    }

    @Test
    @DisplayName("재고 차감 실패: 잔여 수량보다 많은 수량을 요청하면 SoldOutException이 발생한다")
    void decreaseQuantity_soldOut() {
        // given
        Event event = new Event();
        event.setId(1L);
        event.setRemainingQuantity(5);

        // when & then
        assertThatThrownBy(() -> event.decreaseQuantity(10))
                .isInstanceOf(SoldOutException.class);
    }
}