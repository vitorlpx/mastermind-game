# Estágio 1 — Build
FROM node:22-alpine AS build

WORKDIR /app

# Copia package.json primeiro para aproveitar cache
COPY package*.json ./

# Instala dependências
RUN npm install

# Copia o código fonte
COPY . .

# Gera o build de produção
RUN npm run build

# Estágio 2 — Serve com Nginx
FROM nginx:alpine

# Copia o build para o Nginx
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

# Configuração do Nginx para Angular (suporte a rotas SPA)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80