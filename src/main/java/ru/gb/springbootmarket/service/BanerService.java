package ru.gb.springbootmarket.service;

import org.springframework.stereotype.Service;
import ru.gb.springbootmarket.model.Baner;
import ru.gb.springbootmarket.repository.BanerRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BanerService {

    private final BanerRepository banerRepository;

    public BanerService(BanerRepository banerRepository) {
        this.banerRepository = banerRepository;
    }


    public List<Baner> getAll() {
        return banerRepository.findAll();
    }

    public void save(Baner baner) {
        banerRepository.save(baner);
    }

    public Optional<Baner> findById(Long id) {
        return banerRepository.findById(id);
    }

    public void deleteById(Long id) {
        banerRepository.deleteById(id);
    }

    public List<Baner> getAllByImageUrl(String imageUrl) {
        return banerRepository.findAllByImageUrl(imageUrl);
    }
}
