package ru.itmo.lab1.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.lab1.model.enums.OrderStatus;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class OrderPatchDto implements Serializable {
    private OrderStatus status;
}
