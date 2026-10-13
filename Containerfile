# AutoFirma Web — multi-stage build (node build -> caddy serve).
# Build context is the repo root: docker build -f Containerfile -t autofirme-web .
FROM node:22-alpine AS build
WORKDIR /app
RUN corepack enable
COPY web/package.json web/pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile
COPY web/ ./
RUN pnpm build

# Static SPA served by caddy; no backend, no runtime Node.
FROM caddy:2-alpine
COPY web/Caddyfile /etc/caddy/Caddyfile
COPY --from=build /app/build /srv
EXPOSE 80
