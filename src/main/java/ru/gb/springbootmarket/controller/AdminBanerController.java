package ru.gb.springbootmarket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.gb.springbootmarket.model.Baner;
import ru.gb.springbootmarket.service.BanerService;
import ru.gb.springbootmarket.service.StorageService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/admin/baner")
public class AdminBanerController {

    private final StorageService storageService;
    private final BanerService banerService;

    public AdminBanerController(StorageService storageService, BanerService banerService) {
        this.storageService = storageService;
        this.banerService = banerService;
    }

    @GetMapping
    public String getAllBaners(Model model) {
        List<Baner> baners = banerService.getAll();
        model.addAttribute("baners", baners);
        return "baners/index";
    }

    @GetMapping("/add")
    public String getBanerAddForm(Model model) {
        model.addAttribute("baner", new Baner());
        return "baners/add_baner_form";
    }

    @PostMapping("/add")
    @Transactional
    public String saveBaner(Baner baner,
                            @RequestParam MultipartFile image,
                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "baners/add_baner_form";
        }
        storageService.store(image);
        baner.setImageUrl("/media/" + image.getOriginalFilename());
        banerService.save(baner);
        return "redirect:/admin/baner";
    }

    @PostMapping("/delete/{id}")
    @Transactional
    public String deleteBaner(@PathVariable Long id) {
        Baner baner = banerService.findById(id).orElse(null);
        if ((baner != null) && (baner.getImageUrl() != null)) {
            List<Baner> banerList = banerService.getAllByImageUrl(baner.getImageUrl());
            Path location = Paths.get(baner.getImageUrl());
            banerService.deleteById(id);
            if (banerList.size() == 1) storageService.delete(location);
        } else banerService.deleteById(id);
        return "redirect:/admin/baner";
    }
}
