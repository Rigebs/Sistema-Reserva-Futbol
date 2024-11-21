import { Injectable } from "@angular/core";

import { AuthConfig, OAuthService } from "angular-oauth2-oidc";
import { AuthService } from "./auth.service";

const oAuthConfig: AuthConfig = {
  issuer: "https://accounts.google.com",
  strictDiscoveryDocumentValidation: false,
  redirectUri: "http://localhost:4200/login/oauth2/code/google",
  clientId:
    "430610015393-cmuv5sg19dvbefjbsk98mtff00fclred.apps.googleusercontent.com",
  scope: "openid profile email",
};

@Injectable({
  providedIn: "root",
})
export class GoogleApiService {
  constructor(
    private readonly oAuthService: OAuthService,
    private authService: AuthService
  ) {
    oAuthService.configure(oAuthConfig);
    oAuthService.loadDiscoveryDocument().then(() => {
      oAuthService.tryLoginImplicitFlow().then(() => {
        if (!oAuthService.hasValidAccessToken()) {
          oAuthService.initLoginFlow();
        } else {
          oAuthService.loadUserProfile().then((userProfile) => {
            authService.getGoogleLoginResponse().subscribe((data) => {
              console.log("D: ", data);
            });
            console.log("d: ", userProfile);
          });
        }
      });
    });
  }
}
