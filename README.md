
### Overview

Hiccup is an Android library that uses HTTP semantics in ContentProviders to make them easier to understand, use, and
maintain.

ContentProviders are similar to web service apis. They both communicate via URIs and allow client applications to read,
create, and modify resources. So the idea is that we can think of them more like web service apis and borrow some best
practices from the web.

### Goals

1. simplify ContentProvider code & readability
1. separate client/provider concerns
1. decouple persistence implementation (ie, sqlite) from ContentProvider
1. expose a clean & simple api
1. remain compatible within normal ContentProvider behavior

#### Status
Alpha, work in progress. Use at your own discretion. Currently supports:

* GET
* POST
* PUT
* DELETE

#### Motivation

Android's ContentProvider is a great tool for data abstraction and cross-app sharing,
but its interface is confusing and has several limitations. Mainly, its
interface is half REST and half SQL, which result in the following:

1. clients (ie, ContentResolver) are forced to know underlying db schema via sql projections, where clauses, etc.
1. restructuring tables (data normalization) breaks client code
1. table joins are difficult to support when using uri's + sql
1. difficult to represent complex models in flat maps (Cursor or ContentValues)
1. assumes SQL backend (ie, does not easily support NOSQL, file, shared pref, etc.)
1. data/resources cannot be versioned

Hiccup tries to overcome these issues by bringing RESTful and MVC-like concepts to ContentProviders, thereby separating
client/server concerns, allowing UI/data layers to evolve independently, and improving code maintainability.


## Usage

#### CursorAdapter renders UI from response
In this example, let's assume our Activity uses a CursorLoader to make a RESTful request to
``content://com.your.authority/categories/52/products?sort=name``. We get back a Cursor,
which has an **_id** (for CursorAdapters) and **body**, which is the JSON "response" of our
domain models. After deserializing, we can bind our views simply using those domain models.

```Java
@Override
public void bindView(View view, Context context, Cursor cursor) {
    int bodyIndex = cursor.getColumnIndex("body");
    String body = cursor.getString(bodyIndex);
    Product product = myJsonParser.fromJson(body, Product.class);

    TextView productNameView = (TextView) view.findViewById(R.id.product_name);
    productNameView.setText(product.name());
    productPriceView.setText(product.price());
}
```

#### ContentProvider acts as a server to delegate requests

Here, we init Hiccup service and create routes that delegate to Controllers.

```Java
@Override
public boolean onCreate() {
    super.onCreate();
    // ContentAdapters let us define how domain models are converted into a Cursor
    ContentAdapter contentAdapter = new HttpContentAdapter(myJsonParser);
    hiccupService = new HiccupService("com.your.authority")
            .newRoute("categories/#/products", new ProductsCollectionController(contentAdapter));
}

@Override
public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                    String sortOrder) {
    // Hiccup service delegates request to Controller based on route
    return hiccupService.delegateQuery(uri);
}
```

#### Controller finds or modifies resources

This controller is responsible for the products collection for the route: _categories/#/products_.

```Java
public class ProductsCollectionController extends AbstractController<Product> {

    public ProductsCollectionController(ContentAdapter<Product> contentAdapter) {
        super(contentAdapter, Product.class);
        // set some other variables...
    }

    @Override
    public Iterable<Product> handleGet(Uri uri) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String categoryId = uri.getPathSegments().get(1);
        String sort = uri.getQueryParameter("sort");
        Cursor productsCursor = findProductsByCategory(db, categoryId, sort);

        // Convert our SQLiteCursor to list of domain models/POJOs
        return convertCursorToProducts(productsCursor);
    }

    private Cursor findProductsByCategory(SQLiteDatabase db, String categoryId, String sort) {
        return db.query(
            // JOIN across multiple tables and sort
        );
    }
}
```
