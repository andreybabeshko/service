var Router = ReactRouter;
var Route = Router.Route;
var RouteHandler = Router.RouteHandler;

var Login = React.createClass({
    getInitialState: function(){
      return {
          login: '',
          pass: '',
          error: false
      }
    },
    handleChange: function(){
        this.setState({
            login: this.refs.login.value,
            pass: this.refs.pass.value
        });
    },
    handleSubmit: function(event) {
        event.preventDefault();

        // post request to server with this.state
        if(this.state.login == 'admin' && this.state.pass == 'admin'){
            this.props.onUserLogin(true);
            window.location = '#/home';
        }else{
            this.setState({error: true});
        }
    },
    componentWillMount: function() {
        if(this.props.isLogedIn){
            window.location = '#/home';
        }
    },
    render: function() {
        return (
            <div className="well well-lg row">
                <form className="col-md-offset-2 col-md-8 form-horizontal" onSubmit={this.handleSubmit}>
                    <div className="form-group clearfix">
                        <label className="col-md-3 control-label">Login</label>
                        <div className="col-md-9">
                            <input type="text" className="form-control" onChange={this.handleChange} ref="login"/>
                        </div>
                    </div>
                    <div className="form-group clearfix">
                        <label className="col-md-3 control-label">Password</label>
                        <div className="col-md-9">
                            <input type="password" className="form-control" onChange={this.handleChange} ref="pass"/>
                        </div>
                    </div>
                    <div className="form-group clearfix">
                        <div className="col-md-offset-3 col-md-9">
                            <button type="submit" className="btn btn-default">Submit</button>
                        </div>
                    </div>
                </form>
                {this.state.error && <Error/>}
            </div>
        );
    }
});

var Home = React.createClass({
    getInitialState: function(){
        return {
            chart: null,
            shops: []
        }
    },
    handleSelect: function(product, shop){
        var self = this;
        var url = '/api/product/prices/' + product.value;
        shop && (url = url + "/" + shop);
        //rest to get chart

        $.get(url, function(prices){
            var newObj = JSON.parse(prices)[0];
            self.setState({chart: newObj});
        });

    },
    componentWillMount: function() {
        if(!this.props.isLogedIn){
            window.location = '#/login';
        }else{
            var self = this;
            $.get('/api/store', function(stores){
                var newArr = JSON.parse(stores);
                self.setState({shops: newArr});
            });
        }
    },
    render: function() {
        return (
            <div className="home" ref="home">
                < Header onUserSelect={this.handleSelect} shops={this.state.shops}/>
                {this.state.chart && < Chart chart={this.state.chart}/>}
            </div>
        );
    }
});


var Header = React.createClass({
    getInitialState: function(){
        return {
            product: null,
            shop: ''
        }
    },
    handleSelect: function(product){
        this.setState({product: product});
        this.props.onUserSelect(product, this.state.shop);
    },
    selectChangeHandler: function(shop){
        this.setState({shop: shop});
        this.props.onUserSelect(this.state.product, shop);
    },
    render: function() {
        return (
            <div className="header" ref="header">
                <div className="well well-lg row">
                    <form className="form-horizontal" onSubmit={this.handleSubmit}>
                        <div className="form-group row">
                            <div className="col-md-6">
                                <label className="col-md-3 control-label">Product</label>
                                <div className="col-md-9">
                                    <AutocompleteInput onUserSelect={this.handleSelect} ref="search"/>
                                </div>
                            </div>
                            {this.state.product && < ShopSelect onUserSelect={this.selectChangeHandler} shops={this.props.shops} />}
                        </div>
                    </form>
                </div>
            </div>
        );
    }
});

var AutocompleteInput = React.createClass({
    componentDidMount: function(){
        var self = this;
        var url = '/api/product/search/';
        $(ReactDOM.findDOMNode(this)).autocomplete({
            source: function( request, response ) {
                var reqUrl = url + request.term;
                $.get(reqUrl, function(results){
                    var newArr = JSON.parse(results);
                    newArr.map(function(item){
                        item.label = item.name.trim();
                        item.value = item.name.trim();
                    });
                    response(newArr);
                });
            },
            select: function( event, ui ) {
                self.props.onUserSelect(ui.item);
            }
        })
    },
    render: function(){
        return(
            <input type="text" className="form-control" placeholder="Start typing..." />
        )
    }
});

var ShopSelect = React.createClass({
    changeHandler: function(event){
        this.props.onUserSelect(event.target.value);
    },
    render: function() {
        return (
            <div className="col-md-6">
                <label className="col-md-3 control-label">Shop</label>
                <div className="col-md-6">
                    <select onChange={this.changeHandler} className="form-control">
                        <option value="">All</option>
                        {this.props.shops.map(function(shop, i){
                            return <option key={i} value={shop.name}>{shop.name}</option>
                        })}
                    </select>
                </div>
            </div>
        );
    }
});


var Chart = React.createClass({
    getInitialState: function(){
        return {
            chart: null
        }
    },
    createChart: function(data){
        console.log(data);
        if(this.state.chart){
            this.state.chart.destroy();
        }
        if(data){
            var chart = new Highcharts.Chart({
                chart: {
                    renderTo: ReactDOM.findDOMNode(this),
                    defaultSeriesType: 'spline'
                },
                title: {
                    text: 'Monthly Average Price'
                },
                xAxis: {
                    type: 'datetime',
                    dateTimeLabelFormats: {
                        day: '%e of %b'
                    },
                    title: {
                        text: 'Date'
                    }
                },
                yAxis: {
                    title: {
                        text: 'Price'
                    }
                },
                series: [{name:data.name, data: data.price}]
            });
        }
        this.setState({chart: chart});
    },
    componentDidMount: function() {
        this.createChart(this.props.chart);
    },
    componentWillReceiveProps: function(nextProps) {
        this.createChart(nextProps.chart);
    },
    render: function() {
        return (
            <div className="chart" ref="chart"></div>
        );
    }
});


var Error = React.createClass({
    render: function () {
        return (
            <div className="alert alert-danger col-md-offset-2 col-md-8">Login or password is incorrect.</div>
        )
    }
});



var App = React.createClass({
    displayName: "App",
    getInitialState: function(){
        return {
            isLogedIn: false
        }
    },
    componentWillMount: function() {
        if(this.state.isLogedIn){
            window.location = '#/home';
        }else{
            window.location = '#/login';
        }
    },
    handleLogin: function(){
        this.setState({isLogedIn: true});
    },
    render: function () {
        return (
            <div>
                <RouteHandler isLogedIn={this.state.isLogedIn} onUserLogin={this.handleLogin}/>
            </div>
        )
    }
});

var routes = (
    React.createElement(Route, {name: "app", path: "/", handler: App},
        React.createElement(Route, {name: "home", handler: Home}),
        React.createElement(Route, {name: "login", handler: Login}),
        React.createElement(Router.DefaultRoute, {handler: Login})
    )
);

Router.run(routes, function (Handler) {
    ReactDOM.render(React.createElement(Handler, null), document.querySelector('#main .container'));
});




