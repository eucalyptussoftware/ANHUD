Java.perform(function() {
    var CanvasDelegatorImpl = Java.use("com.waze.map.canvas.CanvasDelegatorImpl");
    if (CanvasDelegatorImpl.l) {
        CanvasDelegatorImpl.l.overloads.forEach(function(overload) {
            overload.implementation = function() {
                console.log("CanvasDelegatorImpl.l called with args: " + JSON.stringify(arguments));
                return this.l.apply(this, arguments);
            };
        });
    } else {
        console.log("CanvasDelegatorImpl.l not found");
    }
});
