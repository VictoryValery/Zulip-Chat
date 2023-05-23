# Zulip-Chat
<img width="240" src="/streams_screen.jpg"> <img width="240" src="/users_screen.jpg"> <img width="240" src="/shimmer_collage.jpg">

<img width="240" src="/messages_screen.jpg"> <img width="240" src="/dialog_screen.jpg"> <img width="240" src="/actions_screen.jpg">

Zulip Messenger is a robust mobile application designed for seamless communication. With features like real-time updates using Long Polling and caching of users, channels, and messages using Room the app ensures smooth user experience. The well-structured and easy-to-navigate user interface, designed according to a Figma layout, further enhances the user engagement. Custom view implementation for messages and flex-box reactions brings about uniqueness in app interaction.

The used technology stack includes:

* Navigation is implemented through **Cicerone**
* **Dagger2** is used for Dependency Injection (DI)
* Network interactions are performed using **Retrofit 2** / **OkHttp**
* Message/user/channel caching is done with **Room**
* Presentation layer architecture is **ELM** on **Elmslie**
* Messages and flex-box reactions are fully implemented as **Custom View**
* **Long Polling** is used to handle add/change/delete message events and reactions to them (the only technology supported by Zulip)
* Design is implemented according to the layout in [**Figma**](https://www.figma.com/file/4EHVCEXGMj64JuMefMMWwl/TFS-ANDROID-(Copy)?type=design&node-id=0%3A1&t=7NeDmjMzYneUtGxV-1)
* All screens of the application are equipped with Shimmers and Progress bars

Zulip Messenger - это надежное мобильное приложение, предназначенное для беспрепятственного общения. Благодаря таким функциям, как обновление в режиме реального времени с использованием технологии длительного опроса и кэширование пользователей, каналов и сообщений с использованием Room, обеспечивается плавная работа приложения. Хорошо структурированный и простой в навигации пользовательский интерфейс, разработанный в соответствии с макетом Figma, еще больше повышает вовлеченность пользователей. Реализация пользовательского представления сообщений и реакций с помощью Custom View обеспечивает уникальность взаимодействия с приложением.

Используемый стек:
* Навигация реализована через **Cicerone**
* Для DI используется **Dagger2**
* Сетевое взаимодействие выполняется через **Retrofit 2** / **OkHttp**
* Кэширование сообщений/пользователей/каналов - **Room**
* Архитектура презентационного слоя - **ELM** на **Elmslie**
* Сообщения и flex-box реакций выполнены полностью как **Custom View**
* Для получения событий добавления/изменения/удаления сообщений и реакций на них используется **Long Polling** (единственная поддерживаемая Zulip технология)
* Дизайн реализован в соответствии с макетом [**Figma**](https://www.figma.com/file/4EHVCEXGMj64JuMefMMWwl/TFS-ANDROID-(Copy)?type=design&node-id=0%3A1&t=7NeDmjMzYneUtGxV-1)
* Все экраны приложения используют Shimmers и Progress bars

