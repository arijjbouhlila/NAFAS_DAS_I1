# Gabès EcoWatch — Location Service

This first Location API feature manages monitoring stations through REST endpoints. Swagger UI lists the API and lets you send requests from a browser.

## Run (Windows PowerShell)

Open PowerShell in this folder and run:

```powershell
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"
```

Keep that window open. Open Swagger UI at <http://localhost:8081/swagger-ui/index.html>.

## Try the station endpoints in Swagger UI

For each endpoint, expand its row and click **Try it out**.

1. `POST /api/stations`: paste this JSON and click **Execute**:

   ```json
   {
     "name": "Gabes North Station",
     "latitude": 33.88,
     "longitude": 10.10
   }
   ```

   It should return `201 Created` and a station with an `id`.
2. `GET /api/stations`: click **Execute** to list stations.
3. `GET /api/stations/{id}`: enter the created ID, then click **Execute**.
4. `PUT /api/stations/{id}`: enter that ID and send an updated JSON body. It should return `200 OK`.
5. `DELETE /api/stations/{id}`: enter that ID. It should return `204 No Content`.
6. `GET /api/stations/{id}` after deletion should return `404 Not Found`.

A station name is required. Latitude must be from -90 to 90, and longitude from -180 to 180. Invalid values return `400 Bad Request`.

## Current storage

Stations are held in memory so the API can be built and demonstrated without choosing a database yet. They are cleared when the service stops or restarts. The next persistence step can connect a database once the team selects one.

`GET /hello` remains available as the original validation endpoint.
