package org.pwr.zrcaw_z4;

import org.pwr.zrcaw_z4.services.TablesService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void setupTable() {
        String tableName = "Note";
        String key = "uuid";

        TablesService tablesService = new TablesService();
        if(!tablesService.isTableCreated(tableName)){
            tablesService.createTable(tableName, key);
        }
    }

}
