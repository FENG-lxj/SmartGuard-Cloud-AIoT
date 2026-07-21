const axios = require('axios');
const fs = require('fs');
const path = require('path');

async function testCaptcha() {
  try {
    const uuid = 'testuuid' + Math.random().toString(36).substring(2, 10);
    console.log('测试UUID:', uuid);

    const response = await axios.post('http://localhost:8023/user/captcha', {
      uuid: uuid
    }, {
      responseType: 'arraybuffer'
    });

    console.log('请求成功，状态码:', response.status);
    console.log('响应头:', response.headers);
    console.log('响应数据长度:', response.data.length);

    // 保存图片到本地
    const imagePath = path.join(__dirname, `captcha-${uuid}.png`);
    fs.writeFileSync(imagePath, Buffer.from(response.data));
    console.log(`验证码图片已保存到: ${imagePath}`);

  } catch (error) {
    console.error('请求失败:', error.message);
    console.error('错误详情:', error);
  }
}

testCaptcha();