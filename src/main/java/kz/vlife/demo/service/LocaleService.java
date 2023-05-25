package kz.vlife.demo.service;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LocaleService {

    public String determineLanguage(Optional<String> language) {
        // todo make check applocation languages
        return language.orElse("ru");
    }
}
