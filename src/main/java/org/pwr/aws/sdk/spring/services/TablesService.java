package org.pwr.aws.sdk.spring.services;

import org.pwr.aws.sdk.spring.dtos.Table;
import org.pwr.aws.sdk.spring.exceptions.ElementAlreadyExistsException;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.util.ArrayList;
import java.util.List;

@Service
public class TablesService {
    private Region region = Region.US_EAST_1;
    private DynamoDbClient ddb = DynamoDbClient.builder().region(region).build();

    public boolean isTableCreated(String tableName) {
        return getAllTableNames().contains(tableName);
    }

    public List<String> getAllTableNames() {
        ListTablesRequest request = ListTablesRequest.builder().build();
        ListTablesResponse response = ddb.listTables(request);
        return response.tableNames();
    }

    public List<Table> getAllTables() {
        List<Table> tables = new ArrayList<>();
        List<String> allTableNames = getAllTableNames();

        for (String tableName : allTableNames) {
            DescribeTableRequest request = DescribeTableRequest.builder().tableName(tableName).build();
            TableDescription tableInfo = ddb.describeTable(request).table();

            Table table = new Table(tableInfo.tableName(), tableInfo.tableSizeBytes(), tableInfo.creationDateTime());
            tables.add(table);
        }
        return tables;
    }

    public void createTable(String tableName, String key) {
        if (isTableCreated(tableName)) throw new ElementAlreadyExistsException();

        DynamoDbWaiter dbWaiter = ddb.waiter();

        CreateTableRequest request = CreateTableRequest.builder()
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName(key)
                        .attributeType(ScalarAttributeType.S)
                        .build())
                .keySchema(KeySchemaElement.builder()
                        .attributeName(key)
                        .keyType(KeyType.HASH)
                        .build())
                .provisionedThroughput(ProvisionedThroughput.builder()
                        .readCapacityUnits(10L)
                        .writeCapacityUnits(10L)
                        .build())
                .tableName(tableName)
                .build();


        CreateTableResponse response = ddb.createTable(request);
        DescribeTableRequest tableRequest = DescribeTableRequest.builder()
                .tableName(tableName)
                .build();

        WaiterResponse<DescribeTableResponse> waiterResponse = dbWaiter.waitUntilTableExists(r -> r.tableName(tableName));
        waiterResponse.matched().response().ifPresent(System.out::println);

    }

}
