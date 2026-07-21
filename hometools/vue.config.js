const { defineConfig } = require("@vue/cli-service");
module.exports = defineConfig({
  transpileDependencies: true,

  // devServer: {
  //   proxy: {
  //     '/captcha/generate': {
  //       secure: false,
  //       target: 'http://118.178.137.70:8080',
  //       changeOrigin: true,
  //       pathRewrite: { '^/captcha': '' },
  //     }
  //   }
  // }
  devServer: {
    allowedHosts: "all",
    host: "0.0.0.0",
    port: 8080,
    https: false,
    client: {
      // 通过 HTTPS/ngrok 访问时，强制用 wss 连接 DevServer 的 HMR WebSocket
      webSocketURL: {
        protocol: "wss",
        hostname: "7ffd1909cc60.ngrok-free.app",
        port: 443,
        pathname: "/ws",
      },
      overlay: {
        errors: true,
        warnings: false,
      },
    },
  },
});
