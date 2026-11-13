server {
    listen 5000 default_server;

    include /etc/nginx/includes/server_params.conf;

    # TrueNAS sets frame-blocking headers, which would prevent the UI from
    # being embedded in the Home Assistant panel.
    proxy_hide_header X-Frame-Options;
    proxy_hide_header Content-Security-Policy;

    # TrueNAS serves its UI below /ui/. Use a relative redirect so the browser
    # stays inside the ingress path.
    location = / {
        allow   172.30.32.2;
        deny    all;
        return  302 ./ui/;
    }

    location / {
        allow   172.30.32.2;
        deny    all;

        proxy_pass {{ .server }};

        # Keep redirects from TrueNAS (e.g. http -> https) inside the ingress path.
        proxy_redirect ~^https?://[^/]+(/.*)$ {{ .entry }}$1;
        proxy_set_header X-Ingress-Path {{ .entry }};

        {{ if .proxy_pass_host }}
          proxy_set_header Host $http_host;
        {{ else }}
          # TrueNAS validates the websocket Origin against its own host.
          proxy_set_header Origin {{ .server }};
        {{ end }}
        {{ if .proxy_pass_real_ip }}
          proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
          proxy_set_header X-Real-IP $remote_addr;
        {{ end }}
        {{ if .verify_ssl }}
          proxy_ssl_verify on;
          proxy_ssl_trusted_certificate /etc/ssl/certs/ca-certificates.crt;
        {{ else }}
          proxy_ssl_verify off;
        {{ end }}

        {{ if .rewrite_paths }}
          # The TrueNAS UI is built for the web root. Rewrite absolute paths so
          # it works below the Home Assistant ingress prefix.
          sub_filter_once  off;
          sub_filter_types text/html application/javascript text/javascript;
          sub_filter '<base href="/ui/">' '<base href="{{ .entry }}/ui/">';
          sub_filter '<base href="/">' '<base href="{{ .entry }}/">';
          sub_filter '"/websocket"' '"{{ .entry }}/websocket"';
          sub_filter "'/websocket'" "'{{ .entry }}/websocket'";
          sub_filter '"/api/current"' '"{{ .entry }}/api/current"';
          sub_filter "'/api/current'" "'{{ .entry }}/api/current'";
        {{ end }}

        include /etc/nginx/includes/proxy_params.conf;
    }
}
