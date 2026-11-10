var exec = require('cordova/exec');

var PlayGamesV2 = {
    initialize: function () {
        return new Promise((resolve, reject) => exec(resolve, reject, 'PlayGamesV2', 'initialize', []));
    },

    isAuthenticated: function () {
        return new Promise((resolve, reject) => exec(resolve, reject, 'PlayGamesV2', 'isAuthenticated', []));
    },

    signIn: function () {
        return new Promise((resolve, reject) => exec(resolve, reject, 'PlayGamesV2', 'signIn', []));
    },

    // Leaderboards
    submitScore: function (leaderboardId, score) {
        return new Promise((resolve, reject) => exec(resolve, reject, 'PlayGamesV2', 'submitScore', [leaderboardId, score]));
    },

    showLeaderboard: function (leaderboardId) {
        return new Promise((resolve, reject) => exec(resolve, reject, 'PlayGamesV2', 'showLeaderboard', [leaderboardId || ""]));
    },

    // Achievements
    unlockAchievement: function (achievementId) {
        return new Promise((resolve, reject) => exec(resolve, reject, 'PlayGamesV2', 'unlockAchievement', [achievementId]));
    },

    incrementAchievement: function (achievementId, steps) {
        return new Promise((resolve, reject) => exec(resolve, reject, 'PlayGamesV2', 'incrementAchievement', [achievementId, steps]));
    },

    showAchievements: function () {
        return new Promise((resolve, reject) => exec(resolve, reject, 'PlayGamesV2', 'showAchievements', []));
    },

    // Cloud Saves (Snapshots)
    saveGame: function (saveName, dataString) {
        return new Promise((resolve, reject) => exec(resolve, reject, 'PlayGamesV2', 'saveGame', [saveName, dataString]));
    },

    loadGame: function (saveName) {
        return new Promise((resolve, reject) => exec(resolve, reject, 'PlayGamesV2', 'loadGame', [saveName]));
    }
};

module.exports = PlayGamesV2;
