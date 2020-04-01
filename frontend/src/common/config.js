function isDevelopment() {
  return process.env.NODE_ENV === 'development';
}

export const TAGGIT_BASE_API_URL = isDevelopment() ? 'http://localhost:9001' : process.env.BASE_API_URL;
