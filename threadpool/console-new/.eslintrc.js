module.exports = {
  env: {
    browser: true,
    es2021: true,
    node: true,
  },
  root: true,
  extends: ['eslint:recommended', 'react-app', 'plugin:prettier/recommended', 'plugin:@typescript-eslint/recommended'],
  overrides: [],
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module',
    ecmaFeatures: {
      jsx: true,
    },
  },
  plugins: ['react', '@typescript-eslint'],
  rules: {
    eqeqeq: 2,
    'no-alert': 2,
    'no-undef': 2,
    'prefer-const': 0,
    'no-use-before-define': 2,
    'react-hooks/exhaustive-deps': 2,
    '@typescript-eslint/no-explicit-any': 0,
    '@typescript-eslint/no-non-null-assertion': 0,
    '@typescript-eslint/no-var-requires': 0,
    'import/no-anonymous-default-export': 0,
    'jsx-a11y/anchor-is-valid': 0,
  },
};
