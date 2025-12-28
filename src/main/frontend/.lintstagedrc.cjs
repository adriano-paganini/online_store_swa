module.exports = {
  '**/*.{js,jsx,ts,tsx,css,scss,html,md,json}': () => ['npm run prettier:write'],
  '**/*.{js,jsx,ts,tsx}': () => ['npm run lint:fix', 'npm run ts:check'],
};
