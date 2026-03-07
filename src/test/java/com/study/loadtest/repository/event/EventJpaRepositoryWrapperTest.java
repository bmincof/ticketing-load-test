package com.study.loadtest.repository.event;

import com.study.loadtest.domain.event.model.Event;
import com.study.loadtest.domain.event.model.EventStatus;
import com.study.loadtest.shared.exception.NoSuchEntityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(EventJpaRepositoryWrapper.class)
class EventJpaRepositoryWrapperTest {

    @Autowired
    private EventJpaRepositoryWrapper eventRepository;

    @Autowired
    private EventJpaRepository jpaRepository;

    @Test
    @DisplayName("존재하는 ID로 조회하면 Event 객체를 반환한다")
    void findById_success() {
        // given
        Event event = new Event();
        event.setName("테스트 이벤트");
        event.setStartAt(OffsetDateTime.now());
        event.setEndAt(OffsetDateTime.now().plusDays(1));
        event.setTotalQuantity(100);
        event.setRemainingQuantity(100);
        event.setStatus(EventStatus.ACTIVE);
        Event saved = jpaRepository.save(event);

        // when
        Event found = eventRepository.findById(saved.getId());

        // then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("테스트 이벤트");
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회하면 NoSuchEntityException이 발생한다")
    void findById_fail() {
        // given
        Long invalidId = 9999L;

        // when & then
        assertThatThrownBy(() -> eventRepository.findById(invalidId))
                .isInstanceOf(NoSuchEntityException.class)
                .hasMessageContaining("Event")
                .hasMessageContaining(invalidId.toString());
    }
}