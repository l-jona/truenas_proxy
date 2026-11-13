This addon creates a proxy to the local web UI of a TrueNAS SCALE server so that you can open it from the Home Assistant sidebar.

Note that this addon does not run TrueNAS itself.

## Configuration

### Option: `server`

Address of the TrueNAS web UI, in the format `http[s]://host:port`, e.g.

- `https://truenas.local:443`
- `https://192.168.0.50:443`
- `http://192.168.0.50:80`

### Option: `proxy_pass_host`

Pass the Home Assistant host as `Host` header to TrueNAS. Default `false`, because
TrueNAS expects its own host (and checks the websocket `Origin` against it).

### Option: `proxy_pass_real_ip`

Pass the client's real IP to TrueNAS via `X-Forwarded-For` / `X-Real-IP`.

### Option: `verify_ssl`

Verify the TrueNAS HTTPS certificate. TrueNAS ships with a self-signed
certificate, so the default is `false`. Enable only with a certificate trusted by the add-on.

### Option: `rewrite_paths`

The TrueNAS UI is built to be served from `/`, while ingress serves it below a
prefix. When `true` (default), absolute paths (`<base href>`, `/websocket`,
`/api/current`) in the HTML/JS are rewritten to the ingress prefix. This is
best-effort and may need adjusting for a given TrueNAS version. If the UI does not
load through the sidebar, set it to `false` and use the direct port (5000) instead.

## Required Dependencies

- Network access to a running TrueNAS SCALE server
