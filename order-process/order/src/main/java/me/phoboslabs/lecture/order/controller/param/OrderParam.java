package me.phoboslabs.lecture.order.controller.param;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderParam {

    private int productId;
    private int stock;
}
