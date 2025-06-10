# 👟 ShoeStore Mobile App

A mobile e-commerce application for browsing and purchasing shoes, inspired by the Nike shopping experience. The app supports product search, filtering, favorites, cart management, order tracking, and secure authentication via email, Google login, and OTP.

## 📱 UI Design (Figma)
[🔗 View on Figma](https://www.figma.com/design/Jwu50t9GMwNsvnP6SdtvEZ/Shoe-Store)

## 📦 Project Repositories

- **Frontend (Android):** [🔗 GitHub - Step Up Mobile App](https://github.com/v2tuan/Step-Up-Shoes-Store-Mobile-App)
- **Backend (Spring Boot):** [🔗 GitHub - Step Up Backend](https://github.com/v2tuan/Step-Up)

## 🚀 Features

- 🔐 **Authentication:** Email/password login, Google Sign-In, OTP verification, JWT token-based authentication
- 🛍️ **Product Browsing:** View products with images, price, description, color, size, and reviews
- 🔍 **Search & Filter:** Search by keyword and filter by color, price, rating
- ❤️ **Favorites:** Add/remove products to/from favorites
- 🛒 **Shopping Cart:** Add products with selected size/color, edit cart items
- 📦 **Order Management:** Place orders and track status (processing, shipped, delivered, canceled)
- 🎟️ **Coupons/Discount Codes:** Apply discount codes during checkout for eligible promotions
- ✍️ **Product Reviews:** Leave and read reviews
- 🏠 **Address Book:** Manage shipping addresses using Google Maps API
- 💳 **Payments:** Checkout with VNPAY integration

## 🛠️ Technologies Used

### Frontend (Android)
- Android Studio (Java)
- Retrofit2
- Google Maps & Geocoder API

### Backend (Spring Boot)
- Spring Boot (Java)
- Spring Security + JWT
- MySQL
- Redis (caching)
- RESTful API design
- VNPAY payment gateway

## ▶️ Getting Started

### Requirements

- **Frontend:**
  - Android Studio Arctic Fox or later
  - Android SDK 30+
  - Emulator or real Android device (API 26+)

- **Backend:**
  - Java 21+
  - Maven
  - MySQL database
  - Redis Server
  - VNPAY sandbox credentials (for testing)

## ▶️ How to Run

### 1. Clone the Repositories

```bash
# Clone Android (Frontend)
git clone https://github.com/v2tuan/Step-Up-Shoes-Store-Mobile-App

# Clone Spring Boot (Backend)
git clone https://github.com/v2tuan/Step-Up

