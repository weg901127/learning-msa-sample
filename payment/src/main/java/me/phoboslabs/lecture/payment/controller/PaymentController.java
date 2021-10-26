package me.phoboslabs.lecture.payment.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@Slf4j
@RestController
@RequestMapping(value = "/payment", produces = MediaType.APPLICATION_JSON_VALUE)
public class PaymentController {

    @PostMapping
    public ResponseEntity<String> processPayment(@RequestBody final long price) {
        // 임의로 지연시간을 준다. (정말 결제하는것처럼) 0~10초 까지
        try {
            Thread.sleep(this.getRandomNumber(0, 10000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final Random rand = new Random();
        // Generate random integers in range 0 to 9
        final int randNo = rand.nextInt(10);
        // 임의로 결제가 실패하는 케이스를 만들어낸다.
        if (randNo == 1) {
            log.error("{}원 결제가 실패했습니다..", price);
            return new ResponseEntity("ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.info("{}원 결제가 완료되었습니다.", price);
        return ResponseEntity.ok("COMPLETE");
    }

    private int getRandomNumber(final int min, final int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
