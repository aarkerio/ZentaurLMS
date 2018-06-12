<!DOCTYPE html>
<html>
<head>
    <title>Something bad happened</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    {% style "/assets/bootstrap/css/bootstrap.min.css" %}
    {% style "/assets/bootstrap/css/bootstrap-theme.min.css" %}
    <style type="text/css">
        html {
            height: 100%;
            min-height: 100%;
            min-width: 100%;
            overflow: hidden;
            width: 100%;
        }
        html body {
            height: 100%;
            margin: 0;
            padding: 0;
            width: 100%;
        }
        html .container-fluid {
            display: table;
            height: 100%;
            padding: 0;
            width: 100%;
        }
        html .row-fluid {
            display: table-cell;
            height: 100%;
            vertical-align: middle;
        }
    </style>
</head>
<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="col-lg-12">
            <div class="centering text-center">
                <div class="text-center">
                    <h1><span class="text-danger">Error: {{status}}</span></h1>
                    <hr>
                    {% if title %}
                      <h2 class="without-margin">{{title}}</h2>
                    {% endif %}
                    {% if message %}
                      <h4 class="text-danger">{{message}}</h4>
                    {% endif %}
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
