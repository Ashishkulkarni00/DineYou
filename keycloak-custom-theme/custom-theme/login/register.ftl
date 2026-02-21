<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('firstName','lastName','email','username','password','password-confirm') displayInfo=false; section>
    <#if section = "header">
    <#elseif section = "form">
    
    <div class="kc-login-main">
        <div class="kc-login-content">

            <div class="nav-header">
                <button class="back-btn" onclick="history.back()">
                   <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="15 18 9 12 15 6"></polyline>
                   </svg>
                </button>
            </div>

            <h1 class="kc-title">Sign Up</h1>
            <div class="kc-subtitle">
                Create an account to start your story.
            </div>

            <form id="kc-register-form" class="kc-form" action="${url.registrationAction}" method="post">
                
                <div class="input-group">
                    <label for="firstName" class="input-label">First Name</label>
                    <input type="text" id="firstName" class="kc-input" name="firstName" 
                           value="${(register.formData.firstName!'')}" placeholder="First Name" />
                </div>

                <div class="input-group">
                    <label for="lastName" class="input-label">Last Name</label>
                    <input type="text" id="lastName" class="kc-input" name="lastName" 
                           value="${(register.formData.lastName!'')}" placeholder="Last Name" />
                </div>

                <div class="input-group">
                    <label for="email" class="input-label">Email</label>
                    <input type="text" id="email" class="kc-input" name="email" 
                           value="${(register.formData.email!'')}" autocomplete="email" placeholder="Email Address" />
                </div>

                <#if !realm.registrationEmailAsUsername>
                    <div class="input-group">
                        <label for="username" class="input-label">Username</label>
                        <input type="text" id="username" class="kc-input" name="username" 
                               value="${(register.formData.username!'')}" autocomplete="username" placeholder="Username" />
                    </div>
                </#if>

                <div class="input-group">
                    <label for="password" class="input-label">Password</label>
                    <input type="password" id="password" class="kc-input" name="password" 
                           autocomplete="new-password" placeholder="Password" />
                </div>

                <div class="input-group">
                    <label for="password-confirm" class="input-label">Confirm Password</label>
                    <input type="password" id="password-confirm" class="kc-input" name="password-confirm" 
                           placeholder="Confirm Password" />
                </div>

                <button class="kc-login-btn" type="submit">
                    Sign Up
                </button>
            </form>

            <div class="kc-footer">
                Already have an account? <a href="${url.loginUrl}">Log In</a>
            </div>

        </div>
    </div>
    </#if>
</@layout.registrationLayout>