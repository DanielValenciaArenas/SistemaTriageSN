const http = require('http');

const data = JSON.stringify({
  correo: 'testadmin@admin.com',
  password: 'password123'
});

const req = http.request('http://localhost:8080/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Content-Length': data.length
  }
}, (res) => {
  let body = '';
  res.on('data', chunk => body += chunk);
  res.on('end', () => {
    console.log('Login status:', res.statusCode);
    try {
      const response = JSON.parse(body);
      if (response.token) {
        console.log('Token received, fetching /usuarios...');
        const usersReq = http.request('http://localhost:8080/usuarios', {
          headers: {
            'Authorization': 'Bearer ' + response.token
          }
        }, (usersRes) => {
          let usersBody = '';
          usersRes.on('data', chunk => usersBody += chunk);
          usersRes.on('end', () => {
             console.log('Usuarios status:', usersRes.statusCode);
             console.log('Usuarios response:', usersBody.substring(0, 200));
          });
        });
        usersReq.end();
      } else {
         console.log('No token:', body);
      }
    } catch(e) {
      console.log('Parse error:', e.message, 'Body:', body);
    }
  });
});

req.on('error', e => console.error(e));
req.write(data);
req.end();
