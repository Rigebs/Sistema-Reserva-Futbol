const fs = require('fs');
const path = require('path');
const successColor = '\x1b[32m%s\x1b[0m';
const checkSign = '\u{2705}';

const envFile = `export const environment = {
    NG_APP_URL_API_AUTH: '${process.env.NG_APP_URL_API_AUTH}',
    NG_APP_URL_ROOT: '${process.env.NG_APP_URL_ROOT}',
    NG_APP_URL_API_GENERAL: '${process.env.NG_APP_URL_API_GENERAL}'
};
`;
const targetPath = path.join(__dirname, './src/environments/environment.ts');

fs.writeFile(targetPath, envFile, (err) => {
    if (err) {
        console.error(err);
        throw err;
    } else {
        console.log(successColor, `${checkSign} Successfully generated environment.development.ts`);
    }
});