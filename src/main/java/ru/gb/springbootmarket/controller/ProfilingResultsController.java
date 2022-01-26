package ru.gb.springbootmarket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.gb.springbootmarket.dto.ProfileItem;
import ru.gb.springbootmarket.service.SampleAspect;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/statistic")
public class ProfilingResultsController {

    @GetMapping
    public String getAllStatistic(Model model) {
        List<ProfileItem> statisticItems = new ArrayList<>();
        statisticItems.add(new ProfileItem("UserService", SampleAspect.getUsWorkTime()));
        statisticItems.add(new ProfileItem("OrderService", SampleAspect.getOsWorkTime()));
        statisticItems.add(new ProfileItem("ProductService", SampleAspect.getPsWorkTime()));
        model.addAttribute("items", statisticItems);
        return "aop/statistic_list";
    }

}
