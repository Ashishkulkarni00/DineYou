# Stage 1: Build Stage
FROM node:22-bookworm-slim AS build

# Set working directory
WORKDIR /usr/src/app

# Copy package files for dependency installation
COPY package*.json ./

# Install ALL dependencies and explicitly install platform-specific binaries
RUN --mount=type=cache,target=/usr/src/app/.npm \
    npm set cache /usr/src/app/.npm && \
    npm ci && \
    npm install @rollup/rollup-linux-x64-gnu lightningcss-linux-x64-gnu --save-optional

# Copy source code
COPY . .

# Build production bundle (creates optimized chunk files)
RUN npm run build

# Stage 2: Production Stage (Serve with Node)
FROM node:22-alpine

WORKDIR /app

# Install serve package globally
RUN npm install -g serve

# Copy built artifacts from Vite (default 'dist' directory)
COPY --from=build /usr/src/app/dist ./dist

# Expose port
EXPOSE 5173

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:5173 || exit 1

# Start serve on port 5173
CMD ["serve", "-s", "dist", "-l", "5173"]
