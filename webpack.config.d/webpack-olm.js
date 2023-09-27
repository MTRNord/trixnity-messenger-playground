config.resolve = {
    fallback: {
        crypto: false,
        fs: false,
        path: false,
        stream: false,
        os: false,
        buffer: false
    }
};

const CopyPlugin = require("copy-webpack-plugin");
config.plugins.push(
    new CopyPlugin({
        patterns: [
            {from: "../../node_modules/@matrix-org/olm/olm.wasm", to: "."},
        ],
    })
)
