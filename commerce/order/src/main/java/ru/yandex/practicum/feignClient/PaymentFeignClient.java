package ru.yandex.practicum.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.PaymentApi;

@FeignClient(name = "payment")
public interface PaymentFeignClient extends PaymentApi {
}
