package com.jonggae.yakku.kafka;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class EventDto {
    private String eventType;
    private Long orderId;
    private Long productId;
    private Long customerId;
    private Map<String, Object> data;

}
