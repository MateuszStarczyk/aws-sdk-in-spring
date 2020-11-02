# Zastosowanie rozwiązań chmurowych w aplikacjach webowych

## Prepare

Launch the following command.

 ```sh
 $ mvn install
```

Create your credentials file at ~/.aws/credentials (C:\Users\USER_NAME\\.aws\credentials for Windows users) and save the following lines after replacing the values with your own.

```sh
[default]
aws_access_key_id = YOUR_ACCESS_KEY_ID
aws_secret_access_key = YOUR_SECRET_ACCESS_KEY
```

## Run

Launch the following commands.

```sh
 $ cd backend
 $ mvn spring-boot:run
 ```

Your app should be up and running on port 8080. 
 
 Open http://127.0.0.1:8080 in your browser and you should see basic Hello World message.
