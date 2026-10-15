# AutoFirma Web — multi-stage build (node build -> caddy serve).
# Build context is the repo root: docker build -f Containerfile -t autofirme-web .
FROM node:22-alpine AS build
WORKDIR /app
RUN corepack enable
COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile
COPY . .
RUN pnpm build

# Static SPA served by caddy; no backend, no runtime Node.
FROM caddy:2-alpine
COPY Caddyfile /etc/caddy/Caddyfile
COPY --from=build /app/build /srv
EXPOSE 80
