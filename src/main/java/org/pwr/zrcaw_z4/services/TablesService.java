package org.pwr.zrcaw_z4.services;

import org.pwr.zrcaw_z4.exceptions.ElementAlreadyExistsException;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.util.List;

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
