package com.example.hmostest.controller;

import com.example.hmostest.bean.Clock;
import com.example.hmostest.dao.ClockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ClockController {

    @Autowired
    private ClockMapper clockMapper;

    @RequestMapping("/setNewClock")
    public void SetNewClock(@RequestParam(value = "clockid", defaultValue = " ") int clockid,
                            @RequestParam(value = "years", defaultValue = " ") int years,
                            @RequestParam(value = "months", defaultValue = "") int months,
                            @RequestParam(value = "days", defaultValue = "") int days,
                            @RequestParam(value = "hours", defaultValue = "") int hours,
                            @RequestParam(value = "minutes", defaultValue = "") int minutes,
                            @RequestParam(value = "sends", defaultValue = "0") int sends,
                            @RequestParam(value = "happened", defaultValue = "0") int happened) {
        Clock clock = new Clock();
        clock.setClockid(clockid);
        clock.setYears(years);
        clock.setMonths(months);
        clock.setDays(days);
        clock.setHours(hours);
        clock.setMinutes(minutes);
        clock.setSends(sends);
        clock.setHappened(happened);
        clockMapper.insert(clock);
    }

    @RequestMapping("/setSends")
    public void SetSends(@RequestParam(value = "clockid", defaultValue = " ") int clockid) {
        Clock clock = clockMapper.selectByPrimaryKey(clockid);
        clock.setSends(1);
        clockMapper.updateByPrimaryKey(clock);
    }

    @RequestMapping("/setHappened")
    public void SetHappened(@RequestParam(value = "clockid", defaultValue = " ") int clockid) {
        Clock clock = clockMapper.selectByPrimaryKey(clockid);
        clock.setHappened(1);
        clockMapper.updateByPrimaryKey(clock);
    }

    @RequestMapping("/deleteClock")
    public void DeleteClock(@RequestParam(value = "clockid", defaultValue = " ") int clockid) {
        clockMapper.deleteByPrimaryKey(clockid);
    }

    @RequestMapping("/searchAllClock")
    public List<Clock> SearchAllClock() {
        return clockMapper.selectAll();
    }
}
