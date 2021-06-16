# Opening Hours

The code in this repository contains a simple REST API that receives JSON data as input
and returns a human readable output as response. The data received is structured
JSON of opening hours for a restaurant and this information gets coerced into a
text understood by humans.

## Preamble

We will write an endpoint that accepts **JSON-formatted** opening hours of a
restaurant as an input and returns the rendered **human readable format** as a text output.

Input JSON consists of keys indicating days of a week and corresponding opening hours
as values. One JSON file includes data for one restaurant.

```
{
    <dayofweek>: <opening hours>
    <dayofweek>: <opening hours>
    ...
}
```

**&lt;dayofweek&gt;**: monday / tuesday / wednesday / thursday / friday / saturday / sunday  
**&lt;opening hours&gt;**: an array of objects containing opening hours. Each object consist of
two keys:  
- type: open or close
- value: opening / closing time as UNIX time (1.1.1970 as a date),  
  e.g. 32400 = 9 AM, 37800 = 10.30 AM,  
  max value is 86399 = 11.59:59 PM


Example: on Mondays a restaurant is open from 9 AM to 8 PM

```
{
    "monday" : [
        {
            "type" : "open",
            "value" : 32400
        },
        {
            "type" : "close",
            "value" : 72000
        }
    ],
    ...
}
```

### _Special cases_

- If a restaurant is closed the whole day, an array of opening hours is empty.
  - “tuesday”: [] means a restaurant is closed on Tuesdays
- A restaurant can be opened and closed multiple times during the same day,
  - E.g. on Mondays from 9 AM - 11 AM and from 1 PM to 5 PM
- A restaurant might not be closed during the same day
  - A restaurant can be opened e.g. on a Friday evening and closed early
    Saturday morning. In that case friday-object includes only the opening time.
    Closing time is part of the saturday-object.
  - When printing opening hours which span between multiple days, closing
    time is always a part of the day when a restaurant was opened (e.g. Friday 8
    PM - 1 AM)

```
{
    "friday" : [
        {
            "type" : "open",
            "value" : 64800
        }
    ],
    “saturday”: [
        {
            "type" : "close",
            "value" : 3600
        },
        {
            "type" : "open",
            "value" : 32400
        },
        {                        # A restaurant is open:
            "type" : "close",    # Friday: 6 PM - 1 AM
            "value" : 39600      # Saturday: 9 AM -11 AM, 4 PM - 11 PM
        },
        {
            "type" : "open",
            "value" : 57600
        },
        {
            "type" : "close",
            "value" : 82800
        }
    ]
}
```

## Running and testing the solution

In order to run this basic example you would need to have the following installed

- Java Development Kit 11 and setup `JAVA_HOME` (Download for your platform) 
- cURL
- Gradle (Optional)

### Steps to follow

The steps listed below assume you are running from a linux shell (Bash), adapt the instructions for your platform.

1. Clone this repository `opening-hours`
2. Navigate to the `opening-hours` directory

    ```bash
    > cd ./opening-hours
    ```
   
   At the root of this directory, you should find a file `build.gradle.kts`.

3. Execute the gradle task to start a `Netty` server

    ```bash
    > ./gradlew bootRun
    ```

4. From a different shell make an HTTP request

    ```bash
    > curl --location --request POST 'http://localhost:8080/opening-hours' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "tuesday": [
            {
                "type": "open",
                "value": 36000
            },
            {
                "type": "close",
                "value": 64800
            }
        ],
        "wednesday": [],
        "monday": [],
        "thursday": [
            {
                "type": "open",
                "value": 37800
            },
            {
                "type": "close",
                "value": 64800
            }
        ],
        "friday": [
            {
                "type": "open",
                "value": 36000
            }
        ],
        "saturday": [
            {
                "type": "close",
                "value": 3600
            },
            {
                "type": "open",
                "value": 36000
            }
        ],
        "sunday": [
            {
                "type": "close",
                "value": 3600
            },
            {
                "type": "open",
                "value": 43200
            },
            {
                "type": "close",
                "value": 75600
            }
        ]
    }'
    ```
   
   You should get the following output
   
   ```bash
   > Monday: Closed
     Tuesday: 10 AM - 6 PM
     Wednesday: Closed
     Thursday: 10:30 AM - 6 PM
     Friday: 10 AM - 1 AM
     Saturday: 10 AM - 1 AM
     Sunday: 12 PM - 9 PM
   ```
   
Next steps: Try adjusting your payload and view the output for different inputs

