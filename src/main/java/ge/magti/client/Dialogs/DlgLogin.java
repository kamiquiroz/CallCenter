package ge.magti.client.Dialogs;


        import com.google.gwt.user.client.Cookies;
        import com.smartgwt.client.types.Alignment;
        import com.smartgwt.client.util.SC;
        import com.smartgwt.client.widgets.Canvas;
        import com.smartgwt.client.widgets.Window;
        import com.smartgwt.client.widgets.form.DynamicForm;
        import com.smartgwt.client.widgets.form.fields.ButtonItem;
        import com.smartgwt.client.widgets.form.fields.PasswordItem;
        import com.smartgwt.client.widgets.form.fields.TextItem;
        import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
        import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
        import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
        import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
        import com.smartgwt.client.widgets.layout.HLayout;
        import ge.magti.client.CallCenter;

        import java.util.Date;

public class DlgLogin extends Window {

    private HLayout hLayout;

    private DynamicForm form;
    private TextItem userNameItem;
    private PasswordItem passwordItem;
    public ButtonItem buttonItem;
    private Canvas mainWindow;

    public DlgLogin() {

        setWidth(280);
        setHeight(130);
        setTitle("Login");
        setShowMinimizeButton(false);
        setIsModal(true);
        setShowModalMask(true);
        setShowCloseButton(false);
        setCanDrag(false);
        setCanDragReposition(false);
        setCanDragResize(false);
        setCanDragScroll(false);
        centerInPage();

        hLayout = new HLayout();
        hLayout.setWidth100();
        hLayout.setHeight100();



        form = new DynamicForm();
        form.setHeight100();
        form.setWidth100();
        form.setPadding(10);
        form.setAutoFocus(true);


        userNameItem = new TextItem();
        userNameItem.setTitle("User Name");
        userNameItem.setWrapTitle(false);
        userNameItem.setWidth("*");

        passwordItem = new PasswordItem();
        passwordItem.setTitle("Password");
        passwordItem.setWidth("*");
        // TODO - Remove.
        passwordItem.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                if (event.getKeyName().equals("Enter")) {
                    login();
                }
            }
        });


        buttonItem = new ButtonItem();
        buttonItem.setTitle("Login");
        buttonItem.setColSpan(2);
        buttonItem.setAlign(Alignment.RIGHT);
        buttonItem.setWidth(80);
//        userNameItem.setHint("თქვენი email-ი, @magticom.ge-ს გარეშე");
        buttonItem.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                login();
            }
        });

        form.setFields(userNameItem, passwordItem,
                buttonItem);
        hLayout.setMembers(form);
        addItem(hLayout);
        setCookieValues();
    }

    public void setMainWindow(Canvas mainWindow) {
        this.mainWindow = mainWindow;
    }

    private void login() {
        // SC.showConsole();
        try {

            String userName = userNameItem.getValueAsString();
            if (userName == null || userName.trim().equals("")) {
                SC.warn("Enter User Name !");
                return;
            }
            String password = passwordItem.getValueAsString();
            if (password == null || password.trim().equals("")) {
                SC.warn("Enter Password !");
                return;
            }
            buttonItem.setDisabled(true);

            CallCenter.callCenterInstance.sendgreet("login\t"+userName+"\t"+password);

            //CallCenter.callCenterInstance.loginSuccess(this);

        } catch (Exception e) {
            e.printStackTrace();
            SC.say(e.toString());
            buttonItem.setDisabled(false);
        }
    }



    private void setCookieValues() {
        String objUserName = Cookies.getCookie("MagticomUserName");
        String objLanguage = Cookies.getCookie("MagticomLanguage");
        String objActiveDir = Cookies.getCookie("MagticomActiveDir");
        if (objUserName != null) {
            userNameItem.setValue(objUserName);
            form.focusInItem(passwordItem);
        }

    }

    private void saveCookies() {
        String objUserName = userNameItem.getValueAsString();
        if (objUserName == null || objUserName.trim().equals("")) {
            return;
        }
        Date now = new Date();
        long nowLong = now.getTime();
        nowLong = nowLong + (1000 * 60 * 60 * 24 * 7);// seven days
        now.setTime(nowLong);
        Cookies.setCookie("MagticomUserName", userNameItem.getValueAsString(),
                now);

    }

    @Override
    public void destroy() {
        super.destroy();
        if (mainWindow != null)
            mainWindow.destroy();
    }
}