import { NgModule } from '@angular/core';
import { AuthModule } from 'angular-auth-oidc-client';


@NgModule({
    imports: [AuthModule.forRoot({
        config: {
            authority: 'https://dev-ozcwmk2f403edtfr.us.auth0.com',
            redirectUrl: window.location.origin,
            clientId: '8ajH9fYpDb02cLRdPs0Jd4WfpoO42VLe',
            scope: 'openid profile offline_access',
            responseType: 'code',
            silentRenew: true,
            useRefreshToken: true,
            secureRoutes: ['http://localhost:8080'],
            customParamsAuthRequest:{
              audience:'http://localhost:8080'
            }
        }
      })],
    providers: [],
    exports: [AuthModule],
})
export class AuthConfigModule {}
