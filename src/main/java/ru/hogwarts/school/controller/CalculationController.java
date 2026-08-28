package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calculation")
public class CalculationController {

    @GetMapping("/sum")
    public long getSum() {
        long n = 1_000_000L;
        long sum = n * (n + 1) / 2;
        return sum;
    }
}