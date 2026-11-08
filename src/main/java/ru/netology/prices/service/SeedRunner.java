package ru.netology.prices.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import ru.netology.prices.config.AppProperties;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class SeedRunner implements ApplicationRunner {

    private final ParseService parseService;
    private final AppProperties props;

    public SeedRunner(ParseService parseService, AppProperties props) {
        this.parseService = parseService;
        this.props = props;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (props.seed().urls() != null && !props.seed().urls().isEmpty()) {
            parseService.submit(props.seed().urls());
        }
    }
}
