### 2.0

- Repurpose the add-on to proxy the TrueNAS SCALE web UI
- Add `verify_ssl` and `rewrite_paths` options, strip frame-blocking headers
- Fix `proxy_pass_real_ip` not being passed to the nginx template
